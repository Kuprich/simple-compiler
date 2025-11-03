import { ref } from 'vue'

const isDarkTheme = ref<boolean>(true)
const THEME_CLASS = 'my-app-dark'

function applyTheme() {
  if (isDarkTheme.value) {
    document.documentElement.classList.add(THEME_CLASS)
  } else {
    document.documentElement.classList.remove(THEME_CLASS)
  }
}

function toggleTheme() {
  isDarkTheme.value = !isDarkTheme.value
  applyTheme()
}

export function useTheme() {
  return { isDarkTheme, toggleTheme, applyTheme, THEME_CLASS }
}
