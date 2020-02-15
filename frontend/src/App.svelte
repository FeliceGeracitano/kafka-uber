<script>
  import Rider from "./components/rider/rider.svelte";
  import Driver from "./components/driver/driver.svelte";
  import { onMount } from "svelte";
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

<div class="container">
  <Rider />
  <Driver />
</div>
