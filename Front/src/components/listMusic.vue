<template>
  <div>
    <ul class="directoryList">
      <li :class="item.list!==undefined?'item directory':'item'" v-for="item in list" :key="item.id">
        <div v-if="item.list" class="itemWrapper">
          <dir-info :names="item.name" :id="item.id" :sync="sync(item.existOnPC,item.existOnPhone)" :path="item.path"></dir-info>
          <list-music :list="item.list"></list-music>
        </div>
        <music-item v-else :names="item.name" :id="item.id" :sync="sync(item.existOnPC,item.existOnPhone)" :path="item.path"></music-item>
      </li>
    </ul>
  </div>
</template>
<script>
  import dirInfo from "./dirInfo";
  import musicItem from "./music-item";
  export default {
      name: 'list-music',
      components: {
          dirInfo,
          musicItem
      },
      props: {
          list: Array
      },
      methods: {
          sync(pc,Phone) { return  pc !== Phone}
      }
  }
</script>
