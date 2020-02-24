<template>
  <li class="item">
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
          <div class="name">{{name}}</div>
          <div class="path">{{path}}</div>
        </div>
        <div class="actionBlocks">
          <div class="actionBlock"><a href="#" class="action action-delete">Удалить</a></div>
          <div class="actionBlock"><a href="#" v-if="sync" class="action action-sync">Синхронизировать</a></div>
        </div>
      </div>
    </div>
  </li>
</template>

<script>
  export default {
      name: 'music-item',
      props: {
          name: String,
          sync: Boolean,
          path: String,
          id: Number
      }
  }
</script>
