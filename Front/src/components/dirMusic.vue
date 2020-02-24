<template>
  <div>
    <ul class="directoryMusic">
      <template v-for="music in musics">
        <directory v-if="music.list" :list="music.list" :name="music.name" :id="music.id" :sync="sync(music.existOnPC,music.existOnPhone)" :path="music.relativePath" :key="music.id"></directory>
        <music-item v-else                              :name="music.name" :id="music.id" :sync="sync(music.existOnPC,music.existOnPhone)" :path="music.relativePath" :key="music.id"></music-item>
      </template>
    </ul>
  </div>
</template>


<script>

  import {mapGetters} from "vuex";
  import directory from "./directory";
  import musicItem from "./music-item";

  export default {
      components: {
          directory,
          musicItem
      },
      state: {
      },
      computed: {
          ...mapGetters('music', {musics: 'all'}),
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
