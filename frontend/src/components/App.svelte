<script>
  import Rider from './Rider.svelte'
  import Driver from './Driver.svelte'
  import { onMount } from 'svelte'
</script>

<style>
  :global(body) {
    padding: 0;
    width: 100%;
    height: 100%;
  }
  .container {
    width: 100%;
    height: 100%;
    display: flex;
  }

  @media only screen and (max-width: 1100px) {
    .container {
      width: 100%;
      height: 100%;
      display: flex;
      flex-direction: column;
    }
  }
</style>

<div class="container">
  <Rider />
  <Driver />
</div>
