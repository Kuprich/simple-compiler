export interface ClassValidationResult {
  filename: string
  error: string
}

/**
 * Extract className from java Code
 */
export function extractClassName(code: string): ClassValidationResult {
  const cleanCode = code
    .replace(/\/\/.*$/gm, '') // Single-line comments
    .replace(/\/\*[\s\S]*?\*\//g, '') // Multi-line comments
    .trim()

  const classMatch = cleanCode.match(/public\s+class\s+(\w+)/)

  if (!classMatch) {
    return {
      filename: '',
      error: 'public class in java code not found!',
    }
  }

  const className = classMatch[1]

  return {
    filename: `${className}.java`,
    error: '',
  }
}
