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
      <div class="actionBlocks">
        <div class="actionBlock">
          <a href="#"  @click="deleteItem" class="action action-delete">Удалить</a>
        </div>
        <div class="actionBlock">
          <a href="#" @click="syncItem" title="SYNC" class="action action-sync">Синхронизировать</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import { store } from "../store/index.js";
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
      sync(pc,Phone) {
        return  pc !== Phone
      },
      syncItem() {
        let arr = document.querySelectorAll("input:checked");
        if (arr.length >= 1) {
          arr = Array.from(arr);
          let idx = arr.map(function(e){return e.name});
          idx = JSON.stringify(idx);
          console.log(idx);
          store.dispatch('music/itemSync', idx);
        }
      },
      deleteItem() {
        let arr = document.querySelectorAll("input:checked");
        if (arr.length >= 1) {
          arr = Array.from(arr);
          let idx = arr.map(function(e){return e.name});
          console.log(idx);
          store.dispatch('music/deleteItems', idx);
        }
      }
    },
    created() {
    },
    mounted() {

    }
  }
</script>

<style>

</style>
