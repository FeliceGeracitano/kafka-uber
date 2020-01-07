<script>
  import UserMap from "./UserMap.svelte";
  import DriverMap from "./DriverMap.svelte";
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
  <UserMap />
  <DriverMap />
</div>
