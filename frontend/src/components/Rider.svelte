<script>
  import Marker from './common/Marker.svelte'
  import mapBox from 'mapbox-gl'
  import Map from './common/Map.svelte'
  import LineString from './common/LineString.svelte'
  import CountDown from './common/CountDown.svelte'
  import CenterView from './common/CenterView.svelte'
  import Actions, { ACTION_TYPE } from '../utils/actions'
  import { webSocket } from 'rxjs/webSocket'
  import { RANDOM_TRIP } from '../utils/constants'
  import { pipe, identity, filter, pathEq, or } from 'ramda'
  import { onMount } from 'svelte'
  import { getUid } from '../utils/utils'
  import { getDirections } from '../utils/mapbox'
  import Container from './common/Container.svelte'

  let riderLocation = RANDOM_TRIP.rider
  const destination = RANDOM_TRIP.destination
  let directionsGeometry = null
  let webSocket$
  let trip
  let driver = {}
  let bounds
  const noop = () => {}

  $: if (directionsGeometry && directionsGeometry.coordinates.length) {
    const coordinates = directionsGeometry.coordinates
    bounds = coordinates
      .filter(Boolean)
      .reduce((bounds, cord) => bounds.extend(cord), new mapBox.LngLatBounds(coordinates[0] || 0, coordinates[0] || 0))
    if (driver && driver.location) bounds.extend([driver.location.lon, driver.location.lat])
  }

  const msgHandlers = {
    handleSyncStatus: async (msg) => {
      trip = JSON.parse(msg.payload)
      const direction = await getDirections(riderLocation, destination)
      directionsGeometry = direction.routes[0].geometry
      if (trip) driver = trip.driver
      if (!trip) webSocket$.next(Actions.rider.requestTrip({ from: riderLocation, to: destination }))
    },
    handleNewDriverLocation: async (msg) => {
      driver = JSON.parse(msg.payload)
      if (trip && trip.status === 'STARTED') {
        riderLocation = driver.location
      }
    },
    handleUpdateTrip: async (msg) => {
      trip = JSON.parse(msg.payload)
    }
  }

  onMount(async () => {
    webSocket$ = webSocket(`ws://localhost:8080/ws-rider/websocket?riderId=${getUid('RIDER')}`)
    webSocket$.multiplex(noop, noop, pathEq(['type'], ACTION_TYPE.SYNC_STATUS)).subscribe(msgHandlers.handleSyncStatus)
    webSocket$
      .multiplex(noop, noop, pathEq(['type'], ACTION_TYPE.UPDATE_DRIVER_LOCATION))
      .subscribe(msgHandlers.handleNewDriverLocation)
    webSocket$.multiplex(noop, noop, pathEq(['type'], ACTION_TYPE.START_TRIP)).subscribe(msgHandlers.handleUpdateTrip)
    webSocket$.multiplex(noop, noop, pathEq(['type'], ACTION_TYPE.CONFIRM_TRIP)).subscribe(msgHandlers.handleUpdateTrip)
  })
</script>

<style>
  .box {
    flex: 1 1 auto;
    display: flex;
    position: relative;
  }

  .map {
    display: flex;
    flex: 1 1 auto;
    box-shadow: 0 1px 20px 3px #0000003d;
  }
  .toolbar {
    display: flex;
    justify-content: space-between;
    position: absolute;
    flex: 1;
    z-index: 99;
    width: 100%;
  }
</style>

<Container title="Rider 👤">
  <div class="box">
    <div class="toolbar" />
    <div class="map">
      <Map lat={riderLocation.lat} lon={riderLocation.lon}>
        <CenterView {bounds} />
        <Marker lat={riderLocation.lat} lon={riderLocation.lon} icon="current-location" />
        {#if driver && driver.location}
          <Marker lat={driver.location.lat} lon={driver.location.lon} icon="driver" />
        {/if}
        <Marker lat={destination.lat} lon={destination.lon} icon="to" />
        <LineString geometry={directionsGeometry} color="#1e88e5" />
      </Map>
    </div>
  </div>
</Container>
