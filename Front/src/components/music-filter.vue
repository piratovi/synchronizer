<template>
  <div class="filterBlock">
    <div class="filterWrapper">
      <div class="actionBlocks">
        <div class="actionBlock"><a href="#" @click="itemConsole" class="action">onlyPC</a></div>
        <div class="actionBlock"><a href="#" class="action active">Directory</a></div>
      </div>
      <div class="filterSelect">
        <select name="pack" id="">
          <option value="all" selected>
            .*
          </option>
          <option v-for="(val,index) in ext" :value="val" :key="val">
            {{index}}
          </option>
        </select>
      </div>
      <div class="actionBlocks"><div class="actionBlock"><a href="#" class="action action-delete">Удалить</a></div><div class="actionBlock"><a href="#" title="SYNC" class="action action-sync">Синхронизировать</a></div></div>
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
      ...mapState('music', {ext: "ext"})
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
