<script>
  import { getContext, afterUpdate } from 'svelte'
  import { mapbox, mapContextKey } from '../../utils/mapbox'

  export let geometry
  export let color = '#323232'

  const { getMap } = getContext(mapContextKey)
  const map = getMap()

  afterUpdate(() => {
    if (!geometry) return
    if (map.getSource('LineString')) map.removeSource('LineString')
    const geojson = { type: 'FeatureCollection', features: [{ type: 'Feature', geometry }] }
    map.addSource('LineString', { type: 'geojson', data: geojson })
    map.addLayer({
      id: 'LineString',
      type: 'line',
      source: 'LineString',
      layout: { 'line-join': 'round', 'line-cap': 'round' },
      paint: { 'line-color': color, 'line-width': 5 }
    })
  })
</script>
