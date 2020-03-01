<template>
  <li class="item directory">
    <div class="itemWrapper">
      <dir-info :names="names" :id="id" :sync="sync" :path="path"></dir-info>
      <list-music :list="list"></list-music>
    </div>
  </li>
</template>

<script>
  import listMusic from "./listMusic";
  import dirInfo from "./dirInfo";

  export default {
      name: 'directory',
      components: {
          listMusic,
          dirInfo
      },
      props: {
          names: String,
          sync: Boolean,
          path: String,
          id: Number,
          list: Array
      }
  }
</script>

<style>

</style>
