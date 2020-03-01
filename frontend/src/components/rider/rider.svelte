<script>
  import { onMount } from "svelte";
  import Map from "../common/Map.svelte";
  import Marker from "../common/Marker.svelte";
  import CenterView from "../common/CenterView.svelte";
  import { getUid } from "../../utils";
  import Actions, { ACTION_TYPE } from "../../actions";
  import LineString from "../common/LineString.svelte";
  import mapBox from "mapbox-gl";
  import { getDirections } from "../../mapbox.js";

  let location = { lat: 45.488561, lon: 9.020773 }; // Cornaredo
  const destination = { lat: 45.505203, lon: 9.093253 }; // Molino dorino
  let directionsGeometry = null;

  let trip;
  let driver = {};
  let bounds;

  $: if (directionsGeometry && directionsGeometry.coordinates.length) {
    const coordinates = directionsGeometry.coordinates;
    bounds = coordinates
      .filter(Boolean)
      .reduce(
        (bounds, cord) => bounds.extend(cord),
        new mapBox.LngLatBounds(coordinates[0] || 0, coordinates[0] || 0)
      );
    if (driver && driver.location)
      bounds.extend([driver.location.lon, driver.location.lat]);
  }

  onMount(async () => {
    const riderId = getUid("RIDER");
    const webSocketConnection = new WebSocket(
      `ws://localhost:8080/ws-rider/websocket?riderId=${riderId}`
    );

    webSocketConnection.onmessage = async message => {
      const data = JSON.parse(message.data);
      switch (data.type) {
        case ACTION_TYPE.SYNC_STATUS:
          trip = JSON.parse(data.payload);
          if (trip) {
            const direction = await getDirections(location, trip.to);
            directionsGeometry = direction.routes[0].geometry;
            driver = trip.driver;
            return;
          }
          const direction = await getDirections(location, destination);
          directionsGeometry = direction.routes[0].geometry;
          webSocketConnection.send(
            Actions.rider.requestTrip(location, destination)
          );
          break;
        case ACTION_TYPE.CONFIRM_TRIP:
          trip = JSON.parse(data.payload);
          // driver confirmed, show driver car
          break;
        case ACTION_TYPE.UPDATE_DRIVER_LOCATION:
          driver = JSON.parse(data.payload);
          if (trip && trip.status === "STARTED") location = driver.location;
          break;
        case ACTION_TYPE.START_TRIP:
          trip = JSON.parse(data.payload);
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
    <Map lat={location.lat} lon={location.lon}>
      <CenterView {bounds} />
      {#if driver && driver.location}
        <Marker
          lat={driver.location.lat}
          lon={driver.location.lon}
          icon="driver" />
      {/if}
      <Marker lat={location.lat} lon={location.lon} icon="current-location" />
      <Marker lat={destination.lat} lon={destination.lon} icon="to" />
      <LineString geometry={directionsGeometry} color="#19C681" />
    </Map>
  </div>
</div>
