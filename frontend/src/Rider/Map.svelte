<script>
  import { onMount, setContext } from "svelte";
  import { mapbox, RiderMapKey } from "../mapbox.js";

  setContext(RiderMapKey, { getMap: () => map });

  export let lat;
  export let lon;
  export let zoom;

  let container;
  let map;

  onMount(() => {
    map = new mapbox.Map({
      container,
      style: "mapbox://styles/mapbox/streets-v9",
      center: [lon, lat],
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
