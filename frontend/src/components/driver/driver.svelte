<script lang="ts">
  import { onMount } from "svelte";
  import Map from "../common/Map.svelte";
  import Actions, { ACTION_TYPE } from "../../actions.ts";
  import Marker from "../common/Marker.svelte";
  import { getUid } from "../../utils.ts";
  import CenterView from "../common/CenterView.svelte";
  import LineString from "../common/LineString.svelte";
  import { getDirections } from "../../mapbox.js";
  import mapbox from "mapbox-gl";
  import Button from "../common/Button.svelte";

  const location = { lat: 45.474507, lon: 8.994964 }; // Bareggio
  let webSocketConnection;
  let from = null;
  let trip = null;
  let to = null;
  let directionsGeometry = null;
  let bounds = null;

  // update bounds of direction
  $: if (directionsGeometry && directionsGeometry.coordinates) {
    const coordinates = directionsGeometry.coordinates;
    bounds = coordinates.reduce(
      (bounds, coord) => bounds.extend(coord),
      new mapbox.LngLatBounds(coordinates[0], coordinates[0])
    );
  }

  onMount(async () => {
    const driverId = getUid("DRIVER");
    webSocketConnection = new WebSocket(
      `ws://localhost:8080/ws-driver/websocket?driverId=${driverId}`
    );

    webSocketConnection.onmessage = async message => {
      const data = JSON.parse(message.data);
      switch (data.type) {
        case ACTION_TYPE.SYNC_STATUS:
          break;
        case ACTION_TYPE.REQUEST_TRIP:
          trip = JSON.parse(data.payload);
          if (!trip) return;
          const direction = await getDirections(location, trip.from, trip.to);
          from = trip.from;
          to = trip.to;
          directionsGeometry = direction.routes[0].geometry;
          break;
        default:
          break;
      }
    };
  });

  const handleClick = () => {
    webSocketConnection.send(Actions.driver.confirmTrip(trip.id, location));
  };
</script>

<style>
  .container {
    display: flex;
    flex-direction: column;
    margin: 1rem;
    position: relative;
  }
  .title {
    padding: 0 0 1rem 0;
  }
  .map {
    box-shadow: 0 1px 20px 3px #0000003d;
    height: 500px;
    width: 500px;
  }
  .toolbar {
    display: flex;
    justify-content: center;
    position: absolute;
    top: 50px;
    width: 100%;
  }
</style>

<div class="container">
  <div class="title">Driver</div>
  <div class="map">
    <Map lat={location.lat} lon={location.lon}>
      <CenterView {bounds} />
      <Marker lat={location.lat} lon={location.lon} icon="current-location" />
      {#if from}
        <Marker lat={from.lat} lon={from.lon} icon="rider" />
      {/if}
      {#if to}
        <Marker lat={to.lat} lon={to.lon} icon="to" />
      {/if}
      <LineString geometry={directionsGeometry} color="#44ACB9" />
    </Map>
  </div>
  <div class="toolbar">
    {#if trip && trip.status === 'REQUESTING'}
      <Button
        style="position:absolute"
        label="Confirm Trip"
        class="btn"
        onClick={handleClick} />
    {/if}
  </div>
</div>
