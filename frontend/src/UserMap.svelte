<script>
  import { onMount, setContext } from "svelte";
  import { mapbox, UserMapKey } from "./mapbox.js";

  setContext(UserMapKey, { getMap: () => map });

  let map;
  let container;

  onMount(() => {
    map = new mapbox.Map({
      container,
      style: "mapbox://styles/mapbox/streets-v11",
      center: [-74.5, 40], // starting position [lng, lat]
      zoom: 9 // starting zoom
    });
    return () => {
      map.remove();
    };
  });

  onMount(() => {
    // Request last status for this ride uid
  });
</script>

<style>
  .map {
    width: 500px;
    height: 500px;
    margin: 1rem;
    box-shadow: 0 1px 20px 3px #0000003d;
  }
</style>

<div class="map" bind:this={container} />
