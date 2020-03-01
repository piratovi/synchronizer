<template>
  <div>
    {{}}
    <ul class="directoryMusic">
      <template v-for="music in items">
        <directory v-if="music.list" :list="music.list" :names="music.name" :id="music.id" :sync="sync(music.existOnPC,music.existOnPhone)" :path="music.relativePath" :key="music.id"></directory>
        <music-item v-else                              :names="music.name" :id="music.id" :sync="sync(music.existOnPC,music.existOnPhone)" :path="music.relativePath" :key="music.id"></music-item>
      </template>
    </ul>
  </div>
</template>


<script>

  import {mapState} from "vuex";
  import directory from "./directory";
  import musicItem from "./music-item";

  export default {
      name: "dir-music",
      components: {
          directory,
          musicItem
      },
      state: {

      },
      computed: {
          ...mapState('music', {items: "items"})
      },
      methods: {
          sync(pc,Phone) { return  pc !== Phone}
      },
      created() {
      },
      mounted() {

      }
  }

</script>


<style>

</style>
