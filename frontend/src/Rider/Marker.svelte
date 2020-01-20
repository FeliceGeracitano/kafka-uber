<script>
  import { getContext } from "svelte";
  import { mapbox, RiderMapKey } from "../mapbox.js";
  const { getMap } = getContext(RiderMapKey);
  const map = getMap();
  export let lat;
  export let lon;
  const popup = new mapbox.Popup({ offset: 25 });
  const marker = new mapbox.Marker()
    .setLngLat([lon, lat])
    .setPopup(popup)
    .addTo(map);
</script>
