<script>
  import { onMount, setContext } from 'svelte'
  import { mapbox, mapContextKey } from '../../utils/mapbox'
  export let lat
  export let lon
  export let zoom

  setContext(mapContextKey, { getMap: () => map })

  let container
  let map

  onMount(() => {
    const center = lon && lat ? [lon, lat] : undefined
    const localRef = new mapbox.Map({
      container,
      style: 'mapbox://styles/felicegeracitano/ck51qxm9o02w41clj5swk76vi',
      center,
      zoom: zoom ? zoom : 15
    })

    localRef.on('load', function() {
      map = localRef
      map.resize()
    })

    return () => {
      map.remove()
    }
  })
</script>

<style>
  div {
    flex: 1 1 auto;
  }
</style>

<div bind:this={container}>
  {#if map}
    <slot />
  {/if}
</div>
