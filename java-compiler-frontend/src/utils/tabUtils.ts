
export function findTabElementByFilename(filename: string): HTMLElement | null {
  const tabs = document.querySelectorAll('.p-tab')
  return Array.from(tabs)
    .find(tab => tab.textContent?.trim() === filename) as HTMLElement | null
}
