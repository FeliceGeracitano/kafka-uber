<script lang="ts">
  import { getContext, afterUpdate, beforeUpdate } from "svelte";
  import { mapContextKey } from "../../mapbox.js";
  import { point } from "@turf/helpers";
  import TurfRhumbBearing from "@turf/rhumb-bearing";

  export let location = null;
  export let prevLocation = null;
  let oldBearing = null;
  let bearing = null;
  let isInit = false;
  const { getMap } = getContext(mapContextKey);
  const map = getMap();

  afterUpdate(() => {
    if (!location) return;

    if (map.isMoving() || map.isZooming() || map.isRotating()) return;

    var point1 = point([location.lon, location.lat]);
    var point2 = point([
      (prevLocation || location).lon,
      (prevLocation || location).lat
    ]);
    var bearing = Math.ceil(TurfRhumbBearing(point2, point1));

    if (bearing !== oldBearing) {
      map.rotateTo(bearing, { duration: 200 });
    } else {
      map.easeTo({
        pitch: 60,
        zoom: 16,
        duration: 0,
        bearing,
        center: [location.lon, location.lat]
      });
    }

    prevLocation = location;
    oldBearing = bearing;
  });
</script>
