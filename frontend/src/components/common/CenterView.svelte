<script lang="ts">
  import { getContext } from "svelte";
  import { mapbox, mapContextKey } from "../../mapbox.js";

  export let locations = [];

  const { getMap } = getContext(mapContextKey);
  const map = getMap();
  const box = locations.map(loc => [loc.lon, loc.lat]);
  map.fitBounds(box, {
    padding: 50
  });
</script>
