package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIdcoach;// Ajuste o package conforme seu projeto:

import java.io.*;
import java.io.IOException;

public final class TocSearch {
    private TocSearch() {}

    public static final String SEARCH_INPUT_ID = "search";
    public static final String TOC_ID = "toc";

    /** Opcional: CSS embutido (ajuda a ficar bonito sem mexer no <head>). */
    public static void printInlineCss(Appendable out) throws IOException {
        out.append("<style>")
                .append(".sr-only{position:absolute;width:1px;height:1px;padding:0;margin:-1px;overflow:hidden;clip:rect(0,0,0,0);border:0}")
                .append(".toc{margin:16px 0}")
                .append(".toc-search{margin-bottom:8px}")
                .append(".search{width:100%;max-width:520px;padding:.5rem .75rem;border:1px solid #ccc;border-radius:8px}")
                .append(".toc-list{margin:8px 0 0 0;padding-left:20px}")
                .append(".toc-list li{margin:.125rem 0}")
                .append("</style>");
    }

    /** Abre o bloco do TOC, já com o input de busca e a <ul id="toc">. */
    public static void openToc(Appendable out) throws IOException {
        out.append("<section class='toc' role='navigation' aria-label='Índice'>\n")
                .append("<div class='toc-search'>\n")
                .append("  <label for='").append(SEARCH_INPUT_ID).append("' class='sr-only'>Filtrar índice</label>\n")
                .append("  <input id='").append(SEARCH_INPUT_ID).append("' class='search' type='search' placeholder='Filtrar (TOC)…'>\n")
                .append("</div>\n")
                .append("<ul id='").append(TOC_ID).append("' class='toc-list'>\n");
    }

    /** Fecha a lista e a section do TOC. */
    public static void closeToc(Appendable out) throws IOException {
        out.append("</ul>\n</section>\n");
    }

    /** Script de filtragem (acentos-insensível; ESC limpa o campo). */
    public static void printSearchScript(Appendable out) throws IOException {
        out.append("<script>")
                .append("document.addEventListener('DOMContentLoaded',()=>{")
                .append(" const input=document.getElementById('").append(SEARCH_INPUT_ID).append("');")
                .append(" const toc=document.getElementById('").append(TOC_ID).append("');")
                .append(" if(!input||!toc) return;")
                .append(" const items=Array.from(toc.querySelectorAll('li'));")
                .append(" const rmAccents=(s)=>{")
                .append("   try{ return s.normalize('NFD').replace(/\\p{Diacritic}/gu,''); }")
                .append("   catch(_){ return s.normalize('NFD').replace(/[\\u0300-\\u036f]/g,''); }")
                .append(" };")
                .append(" const norm=(s)=>rmAccents(String(s||'')).toLowerCase();")
                .append(" function filtra(){")
                .append("   const q=norm(input.value.trim());")
                .append("   for(const li of items){")
                .append("     const txt=norm(li.textContent);")
                .append("     li.style.display=(!q||txt.includes(q))? '':'none';")
                .append("   }")
                .append(" }")
                .append(" let t=null;")
                .append(" input.addEventListener('input',()=>{ clearTimeout(t); t=setTimeout(filtra, 60); });")
                .append(" input.addEventListener('keydown',e=>{ if(e.key==='Escape'){ input.value=''; filtra(); }});")
                .append("});")
                .append("</script>");
    }
}
