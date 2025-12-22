import type { SourceCode } from '@/types/compiler'
import { ref } from 'vue'

export function useFileManagement(initialFiles: SourceCode[] = []) {

  const files = ref<SourceCode[]>([...initialFiles])

  const addFile = (file: SourceCode) => {
    files.value.push(file)
  }

  const renameFile = (oldName: string, newName: string) => {
    const file = files.value.find(f => f.filename === oldName)
    if (file) {
      file.filename = newName
    }
  }

  const deleteFile = (filename: string) => {
    const index = files.value.findIndex((file) => file.filename === filename)
      if (index !== -1) {
        files.value.splice(index, 1)
      }
  }


  const getFileByName = (filename: string) => {
    return files.value.find((f) => f.filename === filename)
  }

  const getFileIndex = (filename: string) => {
    return files.value.findIndex((f) => f.filename === filename)
  }

  const getFileNames = () => {
    return files.value.map((f) => f.filename)
  }

  return {
    files,
    addFile,
    renameFile,
    deleteFile,
    getFileByName,
    getFileIndex,
    getFileNames
  }
}
