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
    const localRef = new mapbox.Map({
      container,
      style: "mapbox://styles/mapbox/streets-v9",
      center,
      zoom: zoom ? zoom : 15
    });

    localRef.on("load", function() {
      map = localRef;
    });

    return () => {
      map.remove();
    };
  });
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
