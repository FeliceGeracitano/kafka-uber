<script>
  import { onMount } from 'svelte'
  export let label
  export let onClick
</script>

<style>
  button {
    margin: 1rem;
    box-shadow: 0 12px 16px 0 rgba(0, 0, 0, 0.24), 0 17px 50px 0 rgba(0, 0, 0, 0.19);
    background-color: #1e88e5;
    border: 1px solid #0d47a1;
    color: white;
    padding: 8px 16px;
    text-align: center;
    text-decoration: none;
    display: inline-block;
    font-size: 22px;
    cursor: pointer;
    transition-duration: 0.4s;
    border-radius: 4px;
  }
</style>

<button class="button" on:click={onClick}>{label}</button>
