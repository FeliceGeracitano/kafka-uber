<script lang="ts">
  import turf from '@turf/turf'
  import throttle from 'lodash-es/throttle'
  import once from 'lodash-es/once'
  import MovingMarker from '../common/MovingMarker.svelte'
  import Marker from '../common/Marker.svelte'
  import mapBox from 'mapbox-gl'
  import Map from '../common/Map.svelte'
  import LineString from '../common/LineString.svelte'
  import CountDown from '../common/CountDown.svelte'
  import CenterView from '../common/CenterView.svelte'
  import Button from '../common/Button.svelte'
  import BackCamera from '../common/BackCamera.svelte'
  import Actions, { ACTION_TYPE } from '../../actions'
  import { webSocket } from 'rxjs/webSocket'
  import { TAXI_INIT_LOCATION, noop, CAMERA } from '../../constants.js'
  import { pipe, identity, filter, pathEq, or } from 'ramda'
  import { onMount } from 'svelte'
  import { getUid } from '../../utils'
  import { getDirections } from '../../mapbox.js'

  let driverLocation = TAXI_INIT_LOCATION
  let webSocket$
  let trip = null
  let route = null
  let bounds = null
  let drivingInterval
  let start
  let timeLeftString = '0:00'
  const speed = 4
  $: tripDuration = route ? route.duration / speed : 0
  $: metersPerSecond = route ? route.distance / tripDuration : null
  $: distance = route ? route.distance : 0
  $: tripStatus = trip ? trip.status : null
  $: riderLocation = tripStatus === 'STARTED' ? driverLocation : trip && trip.from ? trip.from : null
  $: if (route && route.geometry && route.geometry.coordinates.length) {
    const coordinates = route.geometry.coordinates
    bounds = coordinates.reduce(
      (bounds, coord) => bounds.extend(coord),
      new mapBox.LngLatBounds(coordinates[0] || 0, coordinates[0] || 0)
    )
  }
  $: cameraMode = ['STARTED', 'CONFIRMED'].includes(tripStatus) ? CAMERA.BACK : CAMERA.CENTER

  const msgHandlers = {
    [ACTION_TYPE.SYNC_STATUS]: async msg => {
      trip = JSON.parse(msg.payload)
      if (trip.status !== 'CONFIRMED') return
      await fetchDirections()
      animateDriver(0)
    },
    [ACTION_TYPE.REQUEST_TRIP]: async msg => {
      trip = JSON.parse(msg.payload)
      if (!trip) return
      await fetchDirections()
    },
    [ACTION_TYPE.START_TRIP]: async msg => {
      trip = JSON.parse(msg.payload)
    },
    [ACTION_TYPE.CONFIRM_TRIP]: async msg => {
      trip = JSON.parse(msg.payload)
      animateDriver(0)
    }
  }

  onMount(async () => {
    webSocket$ = webSocket(`ws://localhost:8080/ws-driver/websocket?driverId=${getUid('DRIVER')}`)
    webSocket$
      .multiplex(noop, noop, pathEq(['type'], ACTION_TYPE.SYNC_STATUS))
      .subscribe(msgHandlers[ACTION_TYPE.SYNC_STATUS])
    webSocket$
      .multiplex(noop, noop, pathEq(['type'], ACTION_TYPE.REQUEST_TRIP))
      .subscribe(msgHandlers[ACTION_TYPE.REQUEST_TRIP])
    webSocket$
      .multiplex(noop, noop, pathEq(['type'], ACTION_TYPE.START_TRIP))
      .subscribe(msgHandlers[ACTION_TYPE.START_TRIP])
    webSocket$
      .multiplex(noop, noop, pathEq(['type'], ACTION_TYPE.CONFIRM_TRIP))
      .subscribe(msgHandlers[ACTION_TYPE.CONFIRM_TRIP])
  })

  const sendLocationUpdate = throttle(() => {
    webSocket$.next(Actions.driver.updateLocation(driverLocation))
  }, 1000)

  const handleClick = () => {
    webSocket$.next(Actions.driver.confirmTrip(trip.id, driverLocation))
  }

  const fetchDirections = async () => {
    const direction = await getDirections(driverLocation, trip.from, trip.to)
    route = direction.routes[0]
  }

  const animateDriver = async timestamp => {
    if (!start) start = timestamp
    var progressSeconds = (timestamp - start) / 1000
    var progressMeter = progressSeconds * metersPerSecond
    let timeLeft = tripDuration - timestamp / 1000
    timeLeftString = `${Math.floor(timeLeft / 60)}:${Math.floor(timeLeft % 60)}`
    const along = turf.along(route.geometry, progressMeter / 1000, { units: 'kilometers' })
    let [lon, lat] = along.geometry.coordinates
    driverLocation = { lon: lon.toFixed(10), lat: lat.toFixed(10) }
    sendLocationUpdate()
    checkWhenCloseToRider()
    if (progressMeter < distance) requestAnimationFrame(animateDriver)
    if (progressMeter >= distance) endTrip()
  }

  const checkWhenCloseToRider = throttle(() => {
    if (trip.status !== 'CONFIRMED') return
    const distanceFromRider = turf.distance(
      turf.point([driverLocation.lon, driverLocation.lat]),
      turf.point([trip.from.lon, trip.from.lat])
    )
    if (distanceFromRider <= 0.01) startTrip()
  }, 50)
  const endTrip = () => {
    webSocket$.send(Actions.driver.endTrip())
    localStorage.clear()
  }
  const startTrip = once(() => {
    webSocket$.next(Actions.driver.startTrip())
  })
</script>

<style>
  .container {
    flex: 1 1 auto;
    display: flex;
    padding: 2rem;
  }
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
    justify-content: flex-end;
    position: absolute;
    flex: 1;
    z-index: 99;
    width: 100%;
  }
</style>

<div class="container">
  <div class="box">
    <div class="toolbar">
      {#if trip && trip.status !== 'REQUESTING'}
        <CountDown text={timeLeftString} />
      {/if}
      {#if trip && trip.status === 'REQUESTING'}
        <Button label="Confirm Trip" class="btn" onClick={handleClick} />
      {/if}
    </div>
    <div class="map">
      <Map lat={driverLocation.lat} lon={driverLocation.lon}>

        {#if cameraMode === CAMERA.CENTER}
          <CenterView {bounds} />
        {/if}

        {#if cameraMode === CAMERA.BACK}
          <BackCamera location={driverLocation} />
        {/if}

        <Marker lat={driverLocation.lat} lon={driverLocation.lon} icon="current-location" />
        {#if riderLocation}
          <Marker lat={riderLocation.lat} lon={riderLocation.lon} icon="rider" />
        {/if}
        {#if trip && trip.to}
          <Marker lat={trip.to.lat} lon={trip.to.lon} icon="to" />
        {/if}
        <LineString geometry={route ? route.geometry : null} color="#44ACB9" />
      </Map>
    </div>
  </div>
</div>
