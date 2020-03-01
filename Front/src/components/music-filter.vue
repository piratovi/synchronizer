<template>
  <div class="filterBlock">
    <div class="filterWrapper">
      <div class="actionBlocks">
        <div class="actionBlock"><a href="#" @click="itemConsole" class="action">onlyPC</a></div>
        <div class="actionBlock"><a href="#" class="action active">Directory</a></div>
      </div>
      <div class="filterSelect">
        <select name="pack" id="">
          <option value="all" selected>.*</option>
          <option value="mp3">.mp3</option>
          <option value="avi">.avi</option>
        </select>
      </div>
    </div>
  </div>
</template>

<script>
  import {mapState} from "vuex";

  export default {
    name: "dir-music",
    components: {
    },
    state: {
    },
    computed: {
      ...mapState('music', {items: "items"})
    },
    methods: {
      itemConsole() {console.log()},
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
