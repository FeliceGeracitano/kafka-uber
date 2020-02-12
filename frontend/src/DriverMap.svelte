<script>
  import { onMount, setContext, getContext } from "svelte";
  import { mapbox, DriverMapKey } from "./mapbox.js";

  export let location;
  const { lng, lat } = location;
  let userMarker = new mapbox.Marker().setLngLat([lng, lat]);
  window.userMarker = userMarker;
  setContext(DriverMapKey, { getMap: () => map });

  let map;
  let container;

  // TODO: https://svelte.dev/tutorial/context-api

  const updateMarker = position => {
    const { lng, lat } = location;
    userMarker.setLngLat([lng, lat]).addTo(map);
  };

  onMount(() => {
    map = new mapbox.Map({
      container,
      style: "mapbox://styles/mapbox/streets-v11",
      center: [-74.5, 40],
      zoom: 9
    });
    updateMarker({ lng: -74.5, lat: 40 });
    return () => map.remove();
  });

  $: {
    console.log("location changed", location);
  }
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
