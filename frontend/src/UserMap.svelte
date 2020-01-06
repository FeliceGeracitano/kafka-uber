<script>
  import { onMount, setContext } from "svelte";
  import { mapbox, UserMapKey } from "./mapbox.js";

  setContext(UserMapKey, { getMap: () => map });

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
    height: 50vh;
    width: 100vw;
  }
</style>

<div class="map" bind:this={container} />
