import { ref } from "vue"

const isDarkTheme = ref<boolean>(false)
const THEME_CLASS = 'my-app-dark'

function toggleTheme() {

  isDarkTheme.value = !isDarkTheme.value

  if (isDarkTheme.value) {
    document.documentElement.classList.add('my-app-dark')
  } else {
    document.documentElement.classList.remove('my-app-dark')
  }
}

export function useTheme(){
  return {isDarkTheme, toggleTheme, THEME_CLASS}
}
