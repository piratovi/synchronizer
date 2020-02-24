<template>
  <div class="contentPage wrapper">
    <h1>MUSIC PAGE</h1>
    <div class="actionsTitle actionBlocks">
      <div class="actionBlock">
        <a class="action action-big">PC</a>
      </div>
      <div class="actionBlock">
        <a class="action action-big">Mobile</a>
      </div>
    </div>
    <dir-filter></dir-filter>
    <dir-music></dir-music>
  </div>
</template>

<script>
  import dirFilter from "../components/music-filter";
  import dirMusic from "../components/dirMusic";

  export default {
      components: {
          dirMusic,
          dirFilter
      }
  }
</script>





<style>

</style>