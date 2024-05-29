# import numpy as np
#
# def generate_summary(tekst) -> str:
#   kot=tekst+"yes"
#   return kot
from transformers import MarianMTModel, MarianTokenizer

def generate_summary(original_text) -> str:
    try:
      # Załaduj pre-trenowany model BART i tokenizer
      model_name = "Helsinki-NLP/opus-mt-pl-en"
      tokenizer = MarianTokenizer.from_pretrained(model_name)
      model = MarianMTModel.from_pretrained(model_name)


      # Tokenizacja i kodowanie tekstu wejściowego
      inputs = tokenizer(original_text, return_tensors="pt", max_length=1024, truncation=True) #max_length to max sekw tokenów wyjsciowych

      # Generowanie sumaryzacji
      # length_penalty>1 generuje dluzsze sekwencje , num_beams- sekwencje tokenów, które model jednocześnie rozważa podczas generowania. Większa liczba? -> może poprawić różnorodność generowanych wyników
      #early_stopping=True - zatrzyma generowanie gdy model stwierdzi, ze trzeba
      summary_ids = model.generate(inputs["input_ids"], max_length=500, min_length=150, length_penalty=3.0, num_beams=4, early_stopping=True)

      # Dekodowanie wygenerowanej sumaryzacji - z tokenow na czytelny tekst
      summary = tokenizer.decode(summary_ids[0], skip_special_tokens=True)
      return summary
    except Exception as e:
      return f'Error: {str(e)}'