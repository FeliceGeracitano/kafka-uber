<script lang="ts">
  import { onMount } from "svelte";
  import Map from "../common/Map.svelte";
  import Actions, { ACTION_TYPE } from "../../actions.ts";
  import Marker from "../common/Marker.svelte";
  import { getUid } from "../../utils.ts";

  // Molino Dorino
  const lon = 8.9936027;
  const lat = 45.4732452;

  onMount(async () => {
    const driverId = getUid("DRIVER");
    const webSocket = new WebSocket(
      `ws://localhost:8080/ws-driver/websocket?driverId=${driverId}`
    );
    webSocket.onmessage = message => {
      const data = JSON.parse(message.data);
      switch (data.type) {
        case ACTION_TYPE.REQUEST_TRIP:
          break;
        default:
          break;
      }
    };
  });
</script>

<style>
  .container {
    padding: 1rem;
    display: flex;
    flex-direction: column;
  }
  .title {
    padding: 0 0 1rem 0;
  }
  .map {
    width: 500px;
    height: 500px;
    box-shadow: 0 1px 20px 3px #0000003d;
  }
</style>

<div class="container">
  <div class="title">Driver</div>
  <div class="map">
    <Map lat={40} lon={-74.5} zoom={9}>
      <Marker lat={40.2} lon={-74.5} />
    </Map>
  </div>
</div>
