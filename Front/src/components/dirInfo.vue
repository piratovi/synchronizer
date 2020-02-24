<template>
  <div class="entryBlock directoryInfo">
    <div class="dirInfo">
      <div class="field">
        <label>
          <span class="wrap">
            <input :name="id" type="checkbox">
            <span class="caption"></span>
          </span>
        </label>
      </div>
      <div class="title">{{name}}<span class="titlePath" :title="path">!</span></div>
    </div>
    <div class="actionBlocks">
      <div class="actionBlock"><a href="#" class="action action-delete">Удалить</a></div>
      <div class="actionBlock"><a href="#" class="action action-sync" v-if="sync">Синхронизировать</a></div>
    </div>
  </div>
</template>

<script>
  export default {
    props: {
        name: String,
        sync: Boolean,
        path: String,
        id: Number
    }
  }
</script>
