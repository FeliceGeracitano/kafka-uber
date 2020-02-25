<script lang="ts">
  import { getContext, afterUpdate } from "svelte";
  import { mapContextKey } from "../../mapbox.js";

  export let bounds = null;
  const { getMap } = getContext(mapContextKey);
  const map = getMap();

  afterUpdate(() => {
    console.log("bounds", bounds);
    if (!bounds || bounds.isEmpty()) return;
    map.fitBounds(bounds, { padding: 50 });
  });
</script>
