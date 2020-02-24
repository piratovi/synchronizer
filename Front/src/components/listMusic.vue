<template>
  <div>
    <ul class="directoryList" v-for="item in list" :key="item.id">
      <li :class="item.list?'item directory':'item'">
        <div class="itemWrapper">
          <dir-info :name="item.name" :id="item.id" :sync="sync(item.existOnPC,item.existOnPhone)" :path="item.path"></dir-info>
          <list-music :list="item.list"></list-music>
        </div>
      </li>
    </ul>
  </div>
</template>
<script>
  import dirInfo from "./dirInfo";

  export default {
      name: 'list-music',
      components: {
          dirInfo
      },
      props: {
          list: Array
      },
      methods: {
          sync(pc,Phone) { return  pc !== Phone}
      }
  }
</script>
