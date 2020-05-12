<script>
  import { getContext, afterUpdate, beforeUpdate } from 'svelte'
  import { mapContextKey } from '../../utils/mapbox'
  import { point } from '@turf/helpers'
  import TurfRhumbBearing from '@turf/rhumb-bearing'

  export let location = null
  export let prevLocation = null
  let oldBearing = null
  let bearing = null
  let isInit = false
  const { getMap } = getContext(mapContextKey)
  const map = getMap()

  const getBearing = () => {
    const point1 = point([location.lon, location.lat])
    const point2 = point([(prevLocation || location).lon, (prevLocation || location).lat])
    return Math.ceil(TurfRhumbBearing(point2, point1))
  }

  afterUpdate(() => {
    if (!location) return
    if (map.isMoving() || map.isZooming() || map.isRotating()) return
    const center = [location.lon, location.lat]

    // First camera movement
    if (location && !prevLocation) {
      map.flyTo({ pitch: 60, zoom: 18, duration: 400, center })
      prevLocation = location
      return
    }

    // Rotate or easy to next location
    const bearing = getBearing()
    if (bearing !== oldBearing) {
      map.rotateTo(bearing, { duration: 200 })
    } else {
      map.easeTo({ pitch: 60, zoom: 18, duration: 10, bearing, center })
    }
    prevLocation = location
    oldBearing = bearing
  })
</script>
