<script>
  import { afterUpdate, onMount, beforeUpdate } from 'svelte'
  import Marker from './Marker.svelte'
  import turfAlong from '@turf/along'

  export let route
  let oldRoute
  export let icon

  let lat = null
  let lon = null
  let metersTraveled = 0
  let metersPerSecond = 0
  let drivingInterval

  afterUpdate(() => {
    if (!route || route === oldRoute) return
    oldRoute = route
    metersTraveled = 0
    metersPerSecond = route.distance / route.duration
    window.clearInterval(drivingInterval)

    drivingInterval = window.setInterval(() => {
      if (metersTraveled >= route.distance) {
        window.clearInterval(drivingInterval)
      } else {
        const along = turfAlong(route.geometry, metersTraveled / 1000, {
          units: 'kilometers'
        })
        ;[lon, lat] = along.geometry.coordinates
        metersTraveled = metersTraveled + metersPerSecond
      }
    }, 1000)
  })
</script>

<Marker {lat} {lon} {icon} />
