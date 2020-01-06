<script>
  import UserMap from "./UserMap.svelte";
  import DriverMap from "./DriverMap.svelte";
</script>

<style>
  :global(body) {
    padding: 0;
  }

  .container {
    height: 100%;
    width: 100%;
    margin: 0;
    padding: 0;
  }
  @media (min-width: 640px) {
  }
</style>

<div class="container">
  <UserMap class="user-map" />
  <DriverMap class="driver-map" />
</div>
