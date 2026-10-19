#!/usr/bin/env python3
"""
Script to read and extract text and markdown from convocatoriaWEBIII.pdf
using PyMuPDF.
"""

import sys
from pathlib import Path
import pymupdf


def extract_pdf_to_text(pdf_path: Path) -> str:
    """Extracts text from all pages of a PDF file."""
    if not pdf_path.exists():
        raise FileNotFoundError(f"PDF file not found at: {pdf_path}")

    doc = pymupdf.open(pdf_path)
    pages_text = []

    for page_index, page in enumerate(doc, start=1):
        text = page.get_text()
        pages_text.append(f"<!-- Página {page_index} -->\n{text.strip()}")

    return "\n\n".join(pages_text)


def main():
    base_dir = Path(__file__).resolve().parent
    pdf_path = base_dir / "convocatoriaWEBIII.pdf"
    
    print(f"Reading: {pdf_path}...")
    try:
        content = extract_pdf_to_text(pdf_path)
        print("Successfully read PDF!")
        print("-" * 50)
        print(content)
        print("-" * 50)
    except Exception as e:
        print(f"Error reading PDF: {e}", file=sys.stderr)
        sys.exit(1)


if __name__ == "__main__":
    main()
