<script lang="ts">
  import { getDirections } from "../../mapbox.js";
  import { getUid } from "../../utils";
  import { onMount } from "svelte";
  import Actions, { ACTION_TYPE } from "../../actions";
  import BackCamera from "../common/BackCamera.svelte";
  import Button from "../common/Button.svelte";
  import CountDown from "../common/CountDown.svelte";
  import CenterView from "../common/CenterView.svelte";
  import LineString from "../common/LineString.svelte";
  import Map from "../common/Map.svelte";
  import mapBox from "mapbox-gl";
  import Marker from "../common/Marker.svelte";
  import MovingMarker from "../common/MovingMarker.svelte";
  import throttle from "lodash-es/throttle";
  import once from "lodash-es/once";
  import turf from "@turf/turf";

  let driverLocation = localStorage.getItem("driverLocation")
    ? JSON.parse(localStorage.getItem("driverLocation"))
    : { lat: 45.474507, lon: 8.994964 }; // Bareggio
  let webSocketConnection;
  let from = null;
  let trip = null;
  let to = null;
  let route = null;
  $: metersPerSecond = route ? route.distance / route.duration : null;
  let bounds = null;
  let drivingInterval;
  let start;
  let timeLeftString = "0:00";
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

  const sendLocationUpdate = throttle(() => {
    webSocketConnection.send(Actions.driver.updateLocation(driverLocation));
  }, 1000);

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
    let timeLeft = route.duration - timestamp / 1000;
    timeLeftString = `${Math.floor(timeLeft / 60)}:${Math.floor(
      timeLeft % 60
    )}`;
    const along = turf.along(route.geometry, progressMeter / 1000, {
      units: "kilometers"
    });
    let [lon, lat] = along.geometry.coordinates;
    driverLocation = { lon, lat };
    localStorage.setItem("driverLocation", JSON.stringify(driverLocation));
    sendLocationUpdate();
    checkWhenCloseToRider();
    if (progressMeter < route.distance) requestAnimationFrame(animateDriver);
    if (progressMeter >= route.distance) endTrip();
  };

  const checkWhenCloseToRider = throttle(() => {
    if (trip.status !== "CONFIRMED") return;
    const distanceFromRider = turf.distance(
      turf.point([driverLocation.lon, driverLocation.lat]),
      turf.point([from.lon, from.lat])
    );
    if (distanceFromRider <= 0.1) startTrip();
  }, 100);

  const endTrip = () => {
    webSocketConnection.send(Actions.driver.endTrip());
    localStorage.clear();
  };
  const startTrip = once(() =>
    webSocketConnection.send(Actions.driver.startTrip())
  );
</script>

<style>
  .container {
    flex: 1 1 auto;
    display: flex;
    padding: 2rem;
  }
  .box {
    flex: 1 1 auto;
    display: flex;
    position: relative;
  }
  .title {
    padding: 0 0 1rem 0;
  }
  .map {
    display: flex;
    flex: 1 1 auto;
    box-shadow: 0 1px 20px 3px #0000003d;
  }
  .toolbar {
    display: flex;
    justify-content: flex-end;
    position: absolute;
    flex: 1;
    z-index: 99;
    width: 100%;
  }
</style>

<div class="container">
  <div class="box">
    <div class="toolbar">
      {#if trip && trip.status !== 'REQUESTING'}
        <CountDown text={timeLeftString} />
      {/if}
      {#if trip && trip.status === 'REQUESTING'}
        <Button label="Confirm Trip" class="btn" onClick={handleClick} />
      {/if}
    </div>
    <!-- <div class="title">Driver</div> -->
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
  </div>
</div>
