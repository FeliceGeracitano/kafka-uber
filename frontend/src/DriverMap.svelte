<script>
  import { onMount, setContext } from "svelte";
  import { mapbox, DriverMapKey } from "./mapbox.js";

  setContext(DriverMapKey, { getMap: () => map });

  let map;
  let container;

  onMount(() => {
    map = new mapbox.Map({
      container,
      style: "mapbox://styles/felicegeracitano/ck51qxm9o02w41clj5swk76vi",
      center: [0, 0]
    });
    return () => {
      map.remove();
    };
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
