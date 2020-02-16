<script lang="ts">
  import { getContext } from "svelte";
  import { mapbox, mapContextKey } from "../../mapbox.js";

  export let bounds = null;
  const { getMap } = getContext(mapContextKey);
  const map = getMap();

  if (bounds) map.fitBounds(bounds, { padding: 50 });
</script>
