<script>
  import turf from '@turf/turf'
  import throttle from 'lodash-es/throttle'
  import once from 'lodash-es/once'
  import MovingMarker from './common/MovingMarker.svelte'
  import Marker from './common/Marker.svelte'
  import mapBox from 'mapbox-gl'
  import Map from './common/Map.svelte'
  import LineString from './common/LineString.svelte'
  import CountDown from './common/CountDown.svelte'
  import CenterView from './common/CenterView.svelte'
  import Button from './common/Button.svelte'
  import FarChaseCameraView from './common/FarChaseCameraView.svelte'
  import Actions, { ACTION_TYPE } from '../utils/actions'
  import { webSocket } from 'rxjs/webSocket'
  import { RANDOM_TRIP, noop, CAMERA } from '../utils/constants'
  import { pipe, identity, filter, pathEq, or } from 'ramda'
  import { onMount } from 'svelte'
  import { getUid, formatSeconds, getTripAmount } from '../utils/utils'
  import { getDirections } from '../utils/mapbox'
  import Continer from './common/Container.svelte'

  let driverLocation = RANDOM_TRIP.driver
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
    [ACTION_TYPE.SYNC_STATUS]: async (msg) => {
      trip = JSON.parse(msg.payload)
      if (trip.status !== 'CONFIRMED') return
      await fetchDirections()
      animateDriver()
    },
    [ACTION_TYPE.REQUEST_TRIP]: async (msg) => {
      trip = JSON.parse(msg.payload)
      if (!trip) return
      await fetchDirections()
    },
    [ACTION_TYPE.START_TRIP]: async (msg) => {
      trip = JSON.parse(msg.payload)
    },
    [ACTION_TYPE.CONFIRM_TRIP]: async (msg) => {
      trip = JSON.parse(msg.payload)
      animateDriver()
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

  const animateDriver = () => {
    const start = new Date().getTime()
    function animate() {
      let timestamp = new Date().getTime() - start
      var progressSeconds = timestamp / 1000
      var progressMeter = progressSeconds * metersPerSecond
      let timeLeft = tripDuration - timestamp / 1000
      timeLeftString = formatSeconds(timeLeft)
      const along = turf.along(route.geometry, progressMeter / 1000, { units: 'kilometers' })
      let [lon, lat] = along.geometry.coordinates
      driverLocation = { lon: lon.toFixed(10), lat: lat.toFixed(10) }
      sendLocationUpdate()
      checkWhenCloseToRider()
      if (progressMeter >= distance) {
        clearInterval(interval)
        endTrip()
      }
    }
    var interval = setInterval(animate, 50)
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
    webSocket$.next(Actions.driver.endTrip(trip.id, getTripAmount(), distance))
    localStorage.clear()
  }
  const startTrip = once(() => {
    webSocket$.next(Actions.driver.startTrip())
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
    justify-content: flex-end;
    position: absolute;
    flex: 1;
    z-index: 99;
    width: 100%;
  }
</style>

<Continer title="Driver 🏎️">
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
          <FarChaseCameraView location={driverLocation} />
        {/if}

        <Marker lat={driverLocation.lat} lon={driverLocation.lon} icon="current-location" />
        {#if riderLocation}
          <Marker lat={riderLocation.lat} lon={riderLocation.lon} icon="rider" />
        {/if}
        {#if trip && trip.to}
          <Marker lat={trip.to.lat} lon={trip.to.lon} icon="to" />
        {/if}
        <LineString geometry={route ? route.geometry : null} color="#1e88e5" />
      </Map>
    </div>
  </div>
</Continer>
