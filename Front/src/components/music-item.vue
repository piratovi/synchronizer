<template>
  <div class="itemWrapper">
    <div class="entryBlock">
      <div class="field">
        <label>
          <span class="wrap">
            <input :name="id" type="checkbox">
            <span class="caption"></span>
          </span>
        </label>
      </div>
      <div class="info">
        <div class="name">{{names}}</div>
        <div class="path">{{path}}</div>
      </div>
      <div class="actionBlocks">
        <!--<div class="actionBlock"><a href="#" class="action action-delete">Удалить</a></div>-->
        <div class="actionBlock"><a href="#" v-if="sync" @click.prevent="itemSync(id)" title="SYNC" class="action action-sync">Синхронизировать</a></div>
      </div>
    </div>
  </div>
</template>

<script>
  import {store} from '../store/index.js'

  export default {
      name: 'music-item',
      props: {
          names: String,
          sync: Boolean,
          path: String,
          id: Number
      },
      methods: {
        itemSync(id) {
          store.dispatch('music/itemSync',[id]);
        },
      }
  }
</script>
