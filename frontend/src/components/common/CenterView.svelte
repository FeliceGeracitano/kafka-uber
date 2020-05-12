<script>
  import { getContext, afterUpdate } from 'svelte'
  import { mapContextKey } from '../../utils/mapbox'

  export let bounds = null
  const { getMap } = getContext(mapContextKey)
  const map = getMap()

  afterUpdate(() => {
    if (!bounds || bounds.isEmpty()) return
    map.fitBounds(bounds, { padding: 50 })
  })
</script>
