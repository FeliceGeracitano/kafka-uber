<script>
  import Rider from "./Rider/Rider.svelte";
  import DriverMap from "./DriverMap.svelte";
  import { onMount } from "svelte";

  // var webSocket = new WebSocket("ws://localhost:8080/ws/websocket");

  webSocket.onopen = function() {
    console.log("connection established");
    debugger;
    webSocket.send(
      JSON.stringify({
        type: "REQUEST_RIDE",
        payload: {
          driver: null,
          rider: { id: "", location: { lat: 0, lng: 0 }, status: "REQUESTING" },
          destination: { lat: 0, lng: 0 }
        }
      })
    );
  };

  webSocket.onclose = function() {
    console.log("connection closed");
  };

  webSocket.onerror = function(err) {
    console.log("error: ", err);
  };

  onMount(async () => {
    const res = await (await fetch("http://localhost:8080/greeting")).json();
    console.log("res", res);
  });

  function handleClick() {
    webSocket.send(JSON.stringify({ type: "say", data: "data" }));
  }
</script>

<style>
  :global(body) {
    padding: 0;
  }
  .container {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
  }
</style>

<div class="container" on:click={handleClick}>
  <DriverMap />
  <Rider />
</div>
