<script>
  import { getContext } from "svelte";
  import { mapbox, mapContextKey } from "../../mapbox.js";
  export let lat;
  export let lon;

  const { getMap } = getContext(mapContextKey);
  const map = getMap();
  const popup = new mapbox.Popup({ offset: 25 });
  const marker = new mapbox.Marker()
    .setLngLat([lon, lat])
    .setPopup(popup)
    .addTo(map);
</script>
