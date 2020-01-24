<script>
  import Map from "./Map.svelte";
  import Marker from "./Marker.svelte";
  import { getUid } from "../utils.js";

  const webSocket = new WebSocket(
    `ws://localhost:8080/ws-rider/websocket?riderUid=${getUid("RIDER")}`
  );
  const riderId = getUid("RIDER");

  webSocket.onopen = function() {
    webSocket.send(
      JSON.stringify({ type: "RIDER_SYNC", payload: { riderId: riderId } })
    );
  };

  webSocket.onmessage = message => {
    switch (message) {
      case messate.type === "RIDE_UDPATE":
        break;
      default:
        break;
    }
  };
</script>

<style>
  .map {
    width: 500px;
    height: 500px;
    margin: 1rem;
    box-shadow: 0 1px 20px 3px #0000003d;
  }
</style>

<div class="map">
  <Map lat={40} lon={-74.5} zoom={9}>
    <Marker lat={40} lon={-74.5} />
  </Map>
</div>
