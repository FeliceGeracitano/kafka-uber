<script>
  import { onMount, setContext } from "svelte";
  import { mapbox, mapContextKey } from "../../mapbox.js";
  export let lat;
  export let lon;
  export let zoom;

  setContext(mapContextKey, { getMap: () => map });

  let container;
  let map;

  onMount(() => {
    const center = lon && lat ? [lon, lat] : undefined;
    map = new mapbox.Map({
      container,
      style: "mapbox://styles/mapbox/streets-v9",
      center,
      zoom
    });
    return () => {
      map.remove();
    };
  });
</script>

<style>
  div {
    width: 100%;
    height: 100%;
  }
</style>

<div bind:this={container}>
  {#if map}
    <slot />
  {/if}
</div>
