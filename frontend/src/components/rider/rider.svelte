<script lang="ts">
  import { onMount } from "svelte";
  import Map from "../common/Map.svelte";
  import Marker from "../common/Marker.svelte";
  import CenterView from "../common/CenterView.svelte";
  import { getUid } from "../../utils.ts";
  import Actions, { ACTION_TYPE } from "../../actions.ts";

  // Bareggio
  const location = {
    lon: 8.9936027,
    lat: 45.4732452
  };
  const destination = {
    lon: 9.0911366,
    lat: 45.5051679
  };
  const zoom = 14;

  onMount(async () => {
    const riderId = getUid("RIDER");
    const webSocket = new WebSocket(
      `ws://localhost:8080/ws-rider/websocket?riderId=${riderId}`
    );

    webSocket.onmessage = message => {
      const data = JSON.parse(message.data);
      switch (data.type) {
        case ACTION_TYPE.SYNC_STATUS:
          if (data.payload === null)
            webSocket.send(Actions.rider.requestTrip(location, destination));
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
  <div class="title">Rider</div>
  <div class="map">
    <Map lat={location.lat} lon={location.lon} {zoom}>
      <CenterView locations={[location, destination]} />
      <Marker lat={location.lat} lon={location.lon} />
      <Marker lat={destination.lat} lon={destination.lon} />
    </Map>
  </div>
</div>
