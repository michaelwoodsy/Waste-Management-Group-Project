<template>
  <div class="keyword-input-container">
    <div class="keyword" v-for="(keyword, index) in keywords"
    :key="'keyword' + index">
      <span>{{  keyword  }}</span>
      <span @click="removeKeyword(index)"><i class="bi bi-x"></i></span>
    </div>
    <input v-model="keywordValue" @keyup.space="addKeyword" />
  </div>
</template>

<script>
export default {
  name: "KeywordInput",

  data() {
    return {
      keywordValue: '',
      keywords: [],
      activeKeyword: null
    }
  },

  methods: {
    addKeyword() {
      if(!this.keywordValue == '' && !this.keywords.includes(this.keywordValue)) {
        this.keywords.push(this.keywordValue);
      }
      this.keywordValue = '';
    },

    removeKeyword(index) {
      this.keywords.splice(index, 1)
    }
  },

}
</script>

<style scoped>
.keyword-input-container {
  width: 100%;
  max-width: 600px;
  padding: 10px;
  background-color: rgba(255, 255, 255, 0.7);
}

input {
  width: 100%;
  padding: 0;
  margin: 0;
  outline: none;
  background-color: transparent;
  font-size: 1rem;
}

.keyword {
  float: left;
  padding: 3px 5px;

  display: flex;
  justify-content: center;
  cursor: pointer;
}

.keyword:hover {
  background-color: #57c340;
  border-radius: 5px;
}

span:first-child {
  margin-right: 8px;
}

</style>