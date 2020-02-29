<script lang="ts">
  import { onMount } from "svelte";
  import Map from "../common/Map.svelte";
  import Actions, { ACTION_TYPE } from "../../actions";
  import Marker from "../common/Marker.svelte";
  import MovingMarker from "../common/MovingMarker.svelte";
  import { getUid } from "../../utils";
  import CenterView from "../common/CenterView.svelte";
  import LineString from "../common/LineString.svelte";
  import { getDirections } from "../../mapbox.js";
  import mapBox from "mapbox-gl";
  import Button from "../common/Button.svelte";
  import turfAlong from "@turf/along";
  import BackCamera from "../common/BackCamera.svelte";

  let driverLocation = { lat: 45.474507, lon: 8.994964 }; // Bareggio
  let webSocketConnection;
  let from = null;
  let trip = null;
  let to = null;
  let route = null;
  $: metersPerSecond = route ? route.distance / route.duration : null;
  let bounds = null;
  let drivingInterval;
  let start;
  $: tripStatus = trip ? trip.status : null;

  // update bounds of direction
  $: if (route && route.geometry && route.geometry.coordinates.length) {
    const coordinates = route.geometry.coordinates;
    bounds = coordinates.reduce(
      (bounds, coord) => bounds.extend(coord),
      new mapBox.LngLatBounds(coordinates[0] || 0, coordinates[0] || 0)
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
          trip = JSON.parse(data.payload);
          if (trip.status !== "CONFIRMED") return;
          await fetchDirections();
          animateDriver(0);
          break;

        case ACTION_TYPE.REQUEST_TRIP:
          trip = JSON.parse(data.payload);
          if (!trip) return;
          await fetchDirections();
          break;

        default:
          break;
      }
    };
  });

  const handleClick = () => {
    webSocketConnection.send(
      Actions.driver.confirmTrip(trip.id, driverLocation)
    );
    trip = { ...trip, status: "CONFIRMED" };
    animateDriver(0);
  };

  const fetchDirections = async () => {
    const direction = await getDirections(driverLocation, trip.from, trip.to);
    from = trip.from;
    to = trip.to;
    route = direction.routes[0];
  };

  const animateDriver = timestamp => {
    if (!start) start = timestamp;
    var progressSeconds = (timestamp - start) / 1000;
    var progressMeter = progressSeconds * metersPerSecond;
    const along = turfAlong(route.geometry, progressMeter / 1000, {
      units: "kilometers"
    });
    let [lon, lat] = along.geometry.coordinates;
    driverLocation = { lon, lat };
    if (progressSeconds < route.duration) requestAnimationFrame(animateDriver);
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
    <Map lat={driverLocation.lat} lon={driverLocation.lon}>

      {#if tripStatus !== 'CONFIRMED'}
        <CenterView {bounds} />
      {/if}

      {#if tripStatus === 'CONFIRMED'}
        <BackCamera location={driverLocation} />
      {/if}

      <Marker
        lat={driverLocation.lat}
        lon={driverLocation.lon}
        icon="current-location" />
      {#if from}
        <Marker lat={from.lat} lon={from.lon} icon="rider" />
      {/if}
      {#if to}
        <Marker lat={to.lat} lon={to.lon} icon="to" />
      {/if}
      <LineString geometry={route ? route.geometry : null} color="#44ACB9" />
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
