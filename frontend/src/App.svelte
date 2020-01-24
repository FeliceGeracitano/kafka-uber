<script>
  import Rider from "./Rider/Rider.svelte";
  import DriverMap from "./DriverMap.svelte";
  import { onMount } from "svelte";

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
