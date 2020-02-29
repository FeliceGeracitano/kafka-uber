<script lang="ts">
  import { getContext, afterUpdate, beforeUpdate } from "svelte";
  import { mapContextKey } from "../../mapbox.js";
  import { point } from "@turf/helpers";
  import TurfRhumbBearing from "@turf/rhumb-bearing";

  export let location = null;
  export let prevLocation = null;
  let isInit = false;
  const { getMap } = getContext(mapContextKey);
  const map = getMap();

  afterUpdate(() => {
    if (!location) return;
    if (map.isZooming() || map.isMoving()) return;
    var point1 = point([location.lon, location.lat]);
    var point2 = point([
      (prevLocation || location).lon,
      (prevLocation || location).lat
    ]);
    var bearing = TurfRhumbBearing(point2, point1);
    map.flyTo({
      speed: 2,
      pitch: 60,
      zoom: 17,
      bearing,
      center: [location.lon, location.lat]
    });
    prevLocation = location;
  });
</script>
