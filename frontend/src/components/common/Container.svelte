<script>
  export let title
</script>

<style>
  .container {
    position: relative;
    flex: 1 1 auto;
    display: flex;
    padding: 2rem;
    margin: 1rem;
    border: 1px solid lightgray;
    border-radius: 2px;
  }

  .title {
    position: absolute;
    top: -13px;
    left: 31px;
    background: white;
    padding: 0 1rem;
    font-size: 1.5rem;
  }
</style>

<div class="container">
  <div class="title">{title}</div>
  <slot />
</div>
